package com.icodesoft.auth.service;

import com.icodesoft.auth.model.LoginUserVO;
import com.icodesoft.auth.model.ResponseModel;

public interface LoginService {
    ResponseModel checkLogin(LoginUserVO loginUserVO);
}
