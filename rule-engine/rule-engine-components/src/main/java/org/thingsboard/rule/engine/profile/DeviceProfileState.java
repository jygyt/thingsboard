/**
 * Copyright © 2016-2020 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.rule.engine.profile;

import lombok.AccessLevel;
import lombok.Getter;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.device.profile.AlarmRule;
import org.thingsboard.server.common.data.device.profile.DeviceProfileAlarm;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.query.EntityKey;
import org.thingsboard.server.common.data.query.KeyFilter;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


class DeviceProfileState {

    private DeviceProfile deviceProfile;
    @Getter(AccessLevel.PACKAGE)
    private final List<DeviceProfileAlarm> alarmSettings = new CopyOnWriteArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    private final Set<EntityKey> entityKeys = ConcurrentHashMap.newKeySet();

    DeviceProfileState(DeviceProfile deviceProfile) {
        updateDeviceProfile(deviceProfile);
    }

    void updateDeviceProfile(DeviceProfile deviceProfile) {
        this.deviceProfile = deviceProfile;
        alarmSettings.clear();
        if (deviceProfile.getProfileData().getAlarms() != null) {
            alarmSettings.addAll(deviceProfile.getProfileData().getAlarms());
            for (DeviceProfileAlarm alarm : deviceProfile.getProfileData().getAlarms()) {
                for (AlarmRule alarmRule : alarm.getCreateRules().values()) {
                    for (KeyFilter keyFilter : alarmRule.getCondition().getCondition()) {
                        entityKeys.add(keyFilter.getKey());
                    }
                }
            }
        }
    }

    public DeviceProfileId getProfileId() {
        return deviceProfile.getId();
    }
}