package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.IdpEnterpriseApplicationAuthorizationPo;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationSelectDto;
import com.g2rain.data.isolation.annotations.IgnoreIsolation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IdpEnterpriseApplicationAuthorizationDao {
    int insert(IdpEnterpriseApplicationAuthorizationPo entity);
    int update(IdpEnterpriseApplicationAuthorizationPo entity);
    int updateByVersion(IdpEnterpriseApplicationAuthorizationPo entity);
    int delete(Long id);

    @IgnoreIsolation
    IdpEnterpriseApplicationAuthorizationPo selectById(Long id);

    @IgnoreIsolation
    List<IdpEnterpriseApplicationAuthorizationPo> selectList(
        IdpEnterpriseApplicationAuthorizationSelectDto selectDto);
}
