INSERT INTO sys_roles (id, role_code, role_name, description)
VALUES (1, 'ADMIN', '系统管理员', '平台管理权限'),
       (2, 'OPERATOR', '运维人员', '设备与工单处理权限')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), description = VALUES(description);

INSERT INTO sys_users (id, username, password, nickname, status)
VALUES (1, 'admin', '{noop}123456', '管理员', 1),
       (2, 'operator', '{noop}123456', '运维员', 1)
ON DUPLICATE KEY UPDATE password = VALUES(password), nickname = VALUES(nickname), status = VALUES(status);

INSERT INTO sys_user_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

INSERT INTO devices (id, device_code, device_name, device_type, status, location, description, last_heartbeat)
VALUES (1, 'DV-10001', '包装线温度传感器A', 'SENSOR', 'ONLINE', '一号车间', '温度采集设备', NOW()),
       (2, 'DV-20001', '冲压机电机B', 'MOTOR', 'FAULT', '二号车间', '电机异常振动', NOW())
ON DUPLICATE KEY UPDATE device_name = VALUES(device_name), status = VALUES(status), updated_at = NOW();

INSERT INTO work_orders (id, order_no, title, description, device_id, assignee_id, priority, status, progress, version, completed_at)
VALUES (1, 'WO202603040001', '温度传感器校准', '温度波动超阈值，需现场校准', 1, 2, 3, 'PROCESSING', 60, 0, NULL),
       (2, 'WO202603040002', '电机振动排查', '二号车间冲压机电机振动告警', 2, 2, 2, 'PENDING', 0, 0, NULL)
ON DUPLICATE KEY UPDATE title = VALUES(title), status = VALUES(status), progress = VALUES(progress), updated_at = NOW();
