package com.boatzhou.mes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boatzhou.mes.common.BusinessException;
import com.boatzhou.mes.common.ErrorCode;
import com.boatzhou.mes.dto.device.DeviceCreateRequest;
import com.boatzhou.mes.dto.device.DeviceUpdateRequest;
import com.boatzhou.mes.entity.Device;
import com.boatzhou.mes.mapper.DeviceMapper;
import com.boatzhou.mes.service.DeviceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * 设备服务实现。
 *
 * <p>缓存策略：</p>
 * <p>1) 设备列表按“查询条件”缓存到 Redis；</p>
 * <p>2) 增删改时递增全局版本号；</p>
 * <p>3) 缓存 key 包含版本号，因此旧缓存会自然失效（不必全量删 key）。</p>
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    /** 设备列表缓存 key 前缀。 */
    private static final String DEVICE_LIST_CACHE_PREFIX = "mes:device:list:";

    /** 设备列表版本号 key（增删改后递增）。 */
    private static final String DEVICE_LIST_VERSION_KEY = "mes:device:list:version";

    private final DeviceMapper deviceMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public DeviceServiceImpl(DeviceMapper deviceMapper,
                             StringRedisTemplate stringRedisTemplate,
                             ObjectMapper objectMapper) {
        this.deviceMapper = deviceMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 创建设备，并通过版本号递增使列表缓存失效。
     */
    @Override
    public Device createDevice(DeviceCreateRequest request) {
        // 约束：设备编码必须唯一。
        Device duplicate = deviceMapper.selectOne(new LambdaQueryWrapper<Device>()
                .eq(Device::getDeviceCode, request.getDeviceCode())
                .last("limit 1"));
        if (duplicate != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "设备编码已存在");
        }

        Device device = new Device();
        device.setDeviceCode(request.getDeviceCode());
        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType());
        device.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "OFFLINE");
        device.setLocation(request.getLocation());
        device.setDescription(request.getDescription());

        if (deviceMapper.insert(device) <= 0) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "设备创建失败");
        }
        bumpDeviceListVersion();
        return device;
    }

    /**
     * 更新设备，并通过版本号递增使列表缓存失效。
     */
    @Override
    public Device updateDevice(Long id, DeviceUpdateRequest request) {
        Device existed = deviceMapper.selectById(id);
        if (existed == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "设备不存在");
        }

        // 如果修改了设备编码，先做唯一性校验。
        if (StringUtils.hasText(request.getDeviceCode()) && !request.getDeviceCode().equals(existed.getDeviceCode())) {
            Device duplicate = deviceMapper.selectOne(new LambdaQueryWrapper<Device>()
                    .eq(Device::getDeviceCode, request.getDeviceCode())
                    .last("limit 1"));
            if (duplicate != null) {
                throw new BusinessException(ErrorCode.CONFLICT, "设备编码已存在");
            }
            existed.setDeviceCode(request.getDeviceCode());
        }

        // 局部更新：仅覆盖本次传入的字段。
        if (StringUtils.hasText(request.getDeviceName())) {
            existed.setDeviceName(request.getDeviceName());
        }
        if (StringUtils.hasText(request.getDeviceType())) {
            existed.setDeviceType(request.getDeviceType());
        }
        if (StringUtils.hasText(request.getStatus())) {
            existed.setStatus(request.getStatus());
        }
        if (request.getLocation() != null) {
            existed.setLocation(request.getLocation());
        }
        if (request.getDescription() != null) {
            existed.setDescription(request.getDescription());
        }

        if (deviceMapper.updateById(existed) <= 0) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "设备更新失败");
        }
        bumpDeviceListVersion();
        return existed;
    }

    /**
     * 删除设备。
     */
    @Override
    public void deleteDevice(Long id) {
        if (deviceMapper.deleteById(id) <= 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "设备不存在");
        }
        bumpDeviceListVersion();
    }

    /**
     * 按 ID 查询设备详情。
     */
    @Override
    public Device getById(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "设备不存在");
        }
        return device;
    }

    /**
     * 条件查询设备列表（带 Redis 缓存）。
     */
    @Override
    public List<Device> list(String status, String deviceType, String keyword) {
        // 读取当前版本号，首次为空则按 0 处理。
        String version = stringRedisTemplate.opsForValue().get(DEVICE_LIST_VERSION_KEY);
        if (!StringUtils.hasText(version)) {
            version = "0";
        }

        // 使用“版本号 + 查询条件”生成稳定缓存 key。
        String source = version + "|" + safeValue(status) + "|" + safeValue(deviceType) + "|" + safeValue(keyword);
        String cacheKey = DEVICE_LIST_CACHE_PREFIX
                + DigestUtils.md5DigestAsHex(source.getBytes(StandardCharsets.UTF_8));

        // 先查缓存。
        String cachedJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(cachedJson)) {
            try {
                return objectMapper.readValue(cachedJson, new TypeReference<List<Device>>() {
                });
            } catch (Exception ignored) {
                // 缓存损坏时回源数据库。
            }
        }

        // 动态拼接查询条件。
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(status), Device::getStatus, status);
        queryWrapper.eq(StringUtils.hasText(deviceType), Device::getDeviceType, deviceType);
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Device::getDeviceName, keyword)
                    .or()
                    .like(Device::getDeviceCode, keyword)
                    .or()
                    .like(Device::getLocation, keyword));
        }
        queryWrapper.orderByDesc(Device::getUpdatedAt);

        List<Device> devices = deviceMapper.selectList(queryWrapper);

        // 回写缓存（15 分钟），缓存失败不影响主流程。
        try {
            stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(devices), Duration.ofMinutes(15));
        } catch (Exception ignored) {
            // no-op
        }

        return devices;
    }

    /**
     * 递增列表版本号，使旧缓存自动失效。
     */
    private void bumpDeviceListVersion() {
        Long value = stringRedisTemplate.opsForValue().increment(DEVICE_LIST_VERSION_KEY);
        if (value != null && value == 1L) {
            // 仅首次创建时设置过期时间，避免该 key 永久驻留。
            stringRedisTemplate.expire(DEVICE_LIST_VERSION_KEY, Duration.ofDays(30));
        }
    }

    /**
     * null 安全处理，保证缓存 key 拼接稳定。
     */
    private String safeValue(String value) {
        return value == null ? "" : value;
    }
}
