package org.back.devsnackshop_back.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.elastic.request.LogAnalyzeRequest;
import org.back.devsnackshop_back.dto.elastic.request.MetricsRequest;
import org.back.devsnackshop_back.dto.serververManage.response.MetricsResponse;
import org.back.devsnackshop_back.entity.elastic.LogAnalyzeDocument;
import org.back.devsnackshop_back.entity.elastic.ServerMetricsDocument;
import org.back.devsnackshop_back.repository.LogAnalyzeRepository;
import org.back.devsnackshop_back.repository.ServerMetricsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class LogAnalyzeService {


    private final LogAnalyzeRepository logAnalyzeRepository;

    // ============================================================
    // 저장
    // ============================================================

    public void save(LogAnalyzeRequest request) {
        LogAnalyzeDocument document = toDocument(request);
        logAnalyzeRepository.save(document);
        log.info("[LogAnalyze] 저장 완료 - serverId: {}", request.getServerId());
    }

    // ============================================================
    // Request → Document 변환
    // ============================================================

    private LogAnalyzeDocument toDocument(LogAnalyzeRequest request) {
        return LogAnalyzeDocument.builder()
                .serverId(request.getServerId())
                .collectedAt(LocalDateTime.now())
                .system(toSystemLog(request.getSystem()))
                .build();
    }

    private LogAnalyzeDocument.SystemLog toSystemLog(LogAnalyzeRequest.SystemLog s) {
        if (s == null) return null;
        return LogAnalyzeDocument.SystemLog.builder()
                .osType(s.getOsType())
                .osVersion(s.getOsVersion())
                .syslog(toSyslogInfo(s.getSyslog()))
                .auth(toAuthInfo(s.getAuth()))
                .kernel(toKernelInfo(s.getKernel()))
                .pkg(toPackageInfo(s.getPkg()))
                .systemd(toSystemdInfo(s.getSystemd()))
                .dmesg(toDmesgInfo(s.getDmesg()))
                .build();
    }

    private LogAnalyzeDocument.SyslogInfo toSyslogInfo(LogAnalyzeRequest.SyslogInfo s) {
        if (s == null) return null;
        return LogAnalyzeDocument.SyslogInfo.builder()
                .errorCount(s.getErrorCount())
                .oomCount(s.getOomCount())
                .cronFail(s.getCronFail())
                .serviceFail(s.getServiceFail())
                .topErrors(toErrorEntries(s.getTopErrors()))
                .build();
    }

    private LogAnalyzeDocument.AuthInfo toAuthInfo(LogAnalyzeRequest.AuthInfo a) {
        if (a == null) return null;
        return LogAnalyzeDocument.AuthInfo.builder()
                .authFail(a.getAuthFail())
                .authSuccess(a.getAuthSuccess())
                .invalidUser(a.getInvalidUser())
                .rootLogin(a.getRootLogin())
                .sudoUsage(a.getSudoUsage())
                .topAttackIps(toAttackIps(a.getTopAttackIps()))
                .build();
    }

    private LogAnalyzeDocument.KernelInfo toKernelInfo(LogAnalyzeRequest.KernelInfo k) {
        if (k == null) return null;
        return LogAnalyzeDocument.KernelInfo.builder()
                .errorCount(k.getErrorCount())
                .segfaultCount(k.getSegfaultCount())
                .diskErrorCount(k.getDiskErrorCount())
                .topErrors(toErrorEntries(k.getTopErrors()))
                .build();
    }

    private LogAnalyzeDocument.PackageInfo toPackageInfo(LogAnalyzeRequest.PackageInfo p) {
        if (p == null) return null;
        return LogAnalyzeDocument.PackageInfo.builder()
                .installCount(p.getInstallCount())
                .removeCount(p.getRemoveCount())
                .errorCount(p.getErrorCount())
                .recentList(toPackageEntries(p.getRecentList()))
                .build();
    }

    private LogAnalyzeDocument.SystemdInfo toSystemdInfo(LogAnalyzeRequest.SystemdInfo s) {
        if (s == null) return null;
        return LogAnalyzeDocument.SystemdInfo.builder()
                .failedCount(s.getFailedCount())
                .failedUnits(toSystemdUnits(s.getFailedUnits()))
                .build();
    }

    private LogAnalyzeDocument.DmesgInfo toDmesgInfo(LogAnalyzeRequest.DmesgInfo d) {
        if (d == null) return null;
        return LogAnalyzeDocument.DmesgInfo.builder()
                .errorCount(d.getErrorCount())
                .topErrors(toErrorEntries(d.getTopErrors()))
                .build();
    }

    // ============================================================
    // 리스트 변환 헬퍼
    // ============================================================

    private List<LogAnalyzeDocument.ErrorEntry> toErrorEntries(List<LogAnalyzeRequest.ErrorEntry> list) {
        if (list == null) return List.of();
        return list.stream()
                .map(e -> LogAnalyzeDocument.ErrorEntry.builder()
                        .count(e.getCount())
                        .message(e.getMessage())
                        .build())
                .collect(Collectors.toList());
    }

    private List<LogAnalyzeDocument.AttackIp> toAttackIps(List<LogAnalyzeRequest.AttackIp> list) {
        if (list == null) return List.of();
        return list.stream()
                .map(e -> LogAnalyzeDocument.AttackIp.builder()
                        .ip(e.getIp())
                        .count(e.getCount())
                        .build())
                .collect(Collectors.toList());
    }

    private List<LogAnalyzeDocument.PackageEntry> toPackageEntries(List<LogAnalyzeRequest.PackageEntry> list) {
        if (list == null) return List.of();
        return list.stream()
                .map(e -> LogAnalyzeDocument.PackageEntry.builder()
                        .action(e.getAction())
                        .packageName(e.getPackageName())
                        .build())
                .collect(Collectors.toList());
    }

    private List<LogAnalyzeDocument.SystemdUnit> toSystemdUnits(List<LogAnalyzeRequest.SystemdUnit> list) {
        if (list == null) return List.of();
        return list.stream()
                .map(e -> LogAnalyzeDocument.SystemdUnit.builder()
                        .unit(e.getUnit())
                        .state(e.getState())
                        .build())
                .collect(Collectors.toList());
    }

    public LogAnalyzeDocument  findLogDataByServerId(long serverId) {

            return logAnalyzeRepository
                    .findFirstByServerIdOrderByCollectedAtDesc(serverId)
                    .orElseThrow(() -> new RuntimeException("로그 데이터 없음. serverId=" + serverId));

    }

    public List<LogAnalyzeDocument> getHistory(Long serverId, int minutes) {

        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        return logAnalyzeRepository
                .findByServerIdAndCollectedAtAfterOrderByCollectedAtDesc(serverId, since);

    }

    public  List<LogAnalyzeDocument> getLogAnalyzeRecent(Long serverId) {
      return  logAnalyzeRepository.findTop200ByServerIdOrderByCollectedAtDesc(serverId);

    }
}
