package com.bianjiahao.es.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bianjiahao.es.entity.Teacher;

import java.util.List;

public interface TeacherMapper extends BaseMapper<Teacher> {


    List<Teacher> query();
}
