package me.zhengjie.repository;

import me.zhengjie.domain.WxCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WxCustomerRepository extends JpaRepository<WxCustomer, Long>, JpaSpecificationExecutor<WxCustomer> {

    /**
     * 根据OpenID查询客户
     *
     * @param openId 微信OpenID
     * @return 客户信息
     */
    Optional<WxCustomer> findByOpenId(String openId);

    /**
     * 根据UnionID查询客户
     *
     * @param unionId 微信UnionID
     * @return 客户信息
     */
    Optional<WxCustomer> findByUnionId(String unionId);

    /**
     * 检查OpenID是否存在
     *
     * @param openId 微信OpenID
     * @return 是否存在
     */
    boolean existsByOpenId(String openId);
}
