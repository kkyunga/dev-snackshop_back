package org.back.devsnackshop_back.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.entity.ServerEntity;
import org.back.devsnackshop_back.entity.SoftWareEntity;
import org.back.devsnackshop_back.mapper.ServerManageMapper;
import org.back.devsnackshop_back.repository.ServerRepository;
import org.back.devsnackshop_back.repository.SoftWareRepository;
import org.back.devsnackshop_back.repository.UserOsInstanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServerManageService {
    private final ServerRepository serverRepository;
    private final SoftWareRepository softWareRepository;
    private final ServerManageMapper serverManageMapper;

    public void createServer(ServerCreateRequest request)
    {

    }
}
