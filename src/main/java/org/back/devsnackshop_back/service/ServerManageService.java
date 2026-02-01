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
        //1. DTO를 ServerEntity로 변환 후 저장
        ServerEntity server = serverManageMapper.toServerEntity(request );
        serverRepository.save(server);


        List<SoftWareEntity> softWareList = serverManageMapper.toSoftwareEntityList(request.getSoftwareToInstall());
        if(softWareList!=null && !softWareList.isEmpty())
        {
            for(SoftWareEntity sw: softWareList){
                sw.setServerId(server.getId());
            }

            softWareRepository.saveAll(softWareList);
        }

    }
}
