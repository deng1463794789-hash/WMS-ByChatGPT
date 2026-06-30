package com.wms.modules.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("SELECT DISTINCT m.* FROM wms_menu m INNER JOIN wms_role_menu rm ON m.id = rm.menu_id WHERE rm.role_id = #{roleId} AND m.status = 'active' ORDER BY m.sort ASC")
    List<SysMenu> findMenusByRoleId(Long roleId);
}
