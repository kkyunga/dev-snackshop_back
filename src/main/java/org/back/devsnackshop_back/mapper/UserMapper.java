package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.user.UserRequest;
import org.back.devsnackshop_back.dto.user.UserResponse;
import org.back.devsnackshop_back.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity -> 프론트 전달용 DTO (비밀번호 제외)
    UserResponse toResponse(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // 필드명이 다른 경우 명시적 매핑
    @Mapping(source = "phone", target = "phoneNumber")
    @Mapping(source = "password", target = "passwordEncrypted")
    UserEntity toEntity(UserRequest userRequest);


}
