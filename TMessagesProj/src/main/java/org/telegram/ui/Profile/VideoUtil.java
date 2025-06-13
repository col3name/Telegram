package org.telegram.ui.Profile;

import static androidx.mediarouter.app.SystemOutputSwitcherDialogController.showDialog;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.Components.ShareAlert;

import java.util.ArrayList;
import java.util.List;

public class VideoUtil {

    public static String getMemoryInfo() {
        int androidVersion = Build.VERSION.SDK_INT;
        int cpuCount = ConnectionsManager.CPU_COUNT;
        int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        long minFreqSum = 0, minFreqCount = 0;
        long maxFreqSum = 0, maxFreqCount = 0;
        long curFreqSum = 0, curFreqCount = 0;
        long capacitySum = 0, capacityCount = 0;
        StringBuilder cpusInfo = new StringBuilder();
        for (int i = 0; i < cpuCount; i++) {
            Long minFreq = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_min_freq");
            Long curFreq = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_cur_freq");
            Long maxFreq = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq");
            Long capacity = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i + "/cpu_capacity");
            cpusInfo.append("#").append(i).append(" ");
            if (minFreq != null) {
                cpusInfo.append("min=").append(minFreq / 1000L).append(" ");
                minFreqSum += (minFreq / 1000L);
                minFreqCount++;
            }
            if (curFreq != null) {
                cpusInfo.append("cur=").append(curFreq / 1000L).append(" ");
                curFreqSum += (curFreq / 1000L);
                curFreqCount++;
            }
            if (maxFreq != null) {
                cpusInfo.append("max=").append(maxFreq / 1000L).append(" ");
                maxFreqSum += (maxFreq / 1000L);
                maxFreqCount++;
            }
            if (capacity != null) {
                cpusInfo.append("cpc=").append(capacity).append(" ");
                capacitySum += capacity;
                capacityCount++;
            }
            cpusInfo.append("\n");
        }
        StringBuilder info = new StringBuilder();
        info.append(Build.MANUFACTURER).append(", ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(", ").append(Build.DEVICE).append(") ").append(" (android ").append(Build.VERSION.SDK_INT).append(")\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            info.append("SoC: ").append(Build.SOC_MANUFACTURER).append(", ").append(Build.SOC_MODEL).append("\n");
        }
        String gpuModel = AndroidUtilities.getSysInfoString("/sys/kernel/gpu/gpu_model");
        if (gpuModel != null) {
            info.append("GPU: ").append(gpuModel);
            Long minClock = AndroidUtilities.getSysInfoLong("/sys/kernel/gpu/gpu_min_clock");
            Long mminClock = AndroidUtilities.getSysInfoLong("/sys/kernel/gpu/gpu_mm_min_clock");
            Long maxClock = AndroidUtilities.getSysInfoLong("/sys/kernel/gpu/gpu_max_clock");
            if (minClock != null) {
                info.append(", min=").append(minClock / 1000L);
            }
            if (mminClock != null) {
                info.append(", mmin=").append(mminClock / 1000L);
            }
            if (maxClock != null) {
                info.append(", max=").append(maxClock / 1000L);
            }
            info.append("\n");
        }
        ConfigurationInfo configurationInfo = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo();
        info.append("GLES Version: ").append(configurationInfo.getGlEsVersion()).append("\n");
        info.append("Memory: class=").append(AndroidUtilities.formatFileSize(memoryClass * 1024L * 1024L));
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(memoryInfo);
        info.append(", total=").append(AndroidUtilities.formatFileSize(memoryInfo.totalMem));
        info.append(", avail=").append(AndroidUtilities.formatFileSize(memoryInfo.availMem));
        info.append(", low?=").append(memoryInfo.lowMemory);
        info.append(" (threshold=").append(AndroidUtilities.formatFileSize(memoryInfo.threshold)).append(")");
        info.append("\n");
        info.append("Current class: ").append(SharedConfig.performanceClassName(SharedConfig.getDevicePerformanceClass())).append(", measured: ").append(SharedConfig.performanceClassName(SharedConfig.measureDevicePerformanceClass()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            info.append(", suggest=").append(Build.VERSION.MEDIA_PERFORMANCE_CLASS);
        }
        info.append("\n");
        info.append(cpuCount).append(" CPUs");
        if (minFreqCount > 0) {
            info.append(", avgMinFreq=").append(minFreqSum / minFreqCount);
        }
        if (curFreqCount > 0) {
            info.append(", avgCurFreq=").append(curFreqSum / curFreqCount);
        }
        if (maxFreqCount > 0) {
            info.append(", avgMaxFreq=").append(maxFreqSum / maxFreqCount);
        }
        if (capacityCount > 0) {
            info.append(", avgCapacity=").append(capacitySum / capacityCount);
        }
        info.append("\n").append(cpusInfo);

        VideoUtil.listCodecs("video/avc", info);
        VideoUtil.listCodecs("video/hevc", info);
        VideoUtil.listCodecs("video/x-vnd.on2.vp8", info);
        VideoUtil.listCodecs("video/x-vnd.on2.vp9", info);
        return info.toString();
    }

    public static void listCodecs(String type, StringBuilder info) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }
        try {
            final int allCodecsCount = MediaCodecList.getCodecCount();
            final List<Integer> decoderIndexes = new ArrayList<>();
            final List<Integer> encoderIndexes = new ArrayList<>();
            boolean first = true;
            for (int i = 0; i < allCodecsCount; ++i) {
                MediaCodecInfo codec = MediaCodecList.getCodecInfoAt(i);
                if (codec == null) {
                    continue;
                }
                String[] types = codec.getSupportedTypes();
                if (types == null) {
                    continue;
                }
                boolean found = false;
                for (int j = 0; j < types.length; ++j) {
                    if (types[j].equals(type)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    (codec.isEncoder() ? encoderIndexes : decoderIndexes).add(i);
                }
            }
            if (decoderIndexes.isEmpty() && encoderIndexes.isEmpty()) {
                return;
            }
            info.append("\n").append(decoderIndexes.size()).append("+").append(encoderIndexes.size()).append(" ").append(type.substring(6)).append(" codecs:\n");
            for (int a = 0; a < decoderIndexes.size(); ++a) {
                if (a > 0) {
                    info.append("\n");
                }
                MediaCodecInfo codec = MediaCodecList.getCodecInfoAt(decoderIndexes.get(a));
                info.append("{d} ").append(codec.getName()).append(" (");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    if (codec.isHardwareAccelerated()) {
                        info.append("gpu"); // as Gpu
                    }
                    if (codec.isSoftwareOnly()) {
                        info.append("cpu"); // as Cpu
                    }
                    if (codec.isVendor()) {
                        info.append(", v"); // as Vendor
                    }
                }
                MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(type);
                info.append("; mi=").append(capabilities.getMaxSupportedInstances()).append(")");
            }
            for (int a = 0; a < encoderIndexes.size(); ++a) {
                if (a > 0 || !decoderIndexes.isEmpty()) {
                    info.append("\n");
                }
                MediaCodecInfo codec = MediaCodecList.getCodecInfoAt(encoderIndexes.get(a));
                info.append("{e} ").append(codec.getName()).append(" (");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    if (codec.isHardwareAccelerated()) {
                        info.append("gpu"); // as Gpu
                    }
                    if (codec.isSoftwareOnly()) {
                        info.append("cpu"); // as Cpu
                    }
                    if (codec.isVendor()) {
                        info.append(", v"); // as Vendor
                    }
                }
                MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(type);
                info.append("; mi=").append(capabilities.getMaxSupportedInstances()).append(")");
            }
            info.append("\n");
        } catch (Exception ignore) {}
    }

}
