package me.zhengjie.repository;

import me.zhengjie.domain.WechatCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WechatCustomerRepository extends JpaRepository<WechatCustomer, Long>, JpaSpecificationExecutor<WechatCustomer> {

    Optional<WechatCustomer> findByOpenId(String openId);

    boolean existsByOpenId(String openId);
}
