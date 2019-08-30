package com.woowacourse.zzinbros.user.web.controller;

import com.woowacourse.zzinbros.common.config.upload.UploadTo;
import com.woowacourse.zzinbros.common.config.upload.UploadedFile;
import com.woowacourse.zzinbros.user.domain.User;
import com.woowacourse.zzinbros.user.dto.*;
import com.woowacourse.zzinbros.user.exception.UserException;
import com.woowacourse.zzinbros.user.service.UserService;
import com.woowacourse.zzinbros.user.web.exception.UserRegisterException;
import com.woowacourse.zzinbros.user.web.support.LoginSessionManager;
import com.woowacourse.zzinbros.user.web.support.SessionInfo;
import com.woowacourse.zzinbros.user.web.support.UserSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final String UPDATE_SUCCESS_MESSAGE = "Modification Success";

    private final UserService userService;
    private final LoginSessionManager loginSessionManager;

    public UserController(UserService userService, LoginSessionManager loginSessionManager) {
        this.userService = userService;
        this.loginSessionManager = loginSessionManager;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> show(@PathVariable("id") Long id) {
        try {
            User user = userService.findUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public String register(UserRequestDto userRequestDto,
                           @UploadedFile UploadTo uploadTo,
                           RedirectAttributes redirectAttr) {
        try {
            userService.register(userRequestDto, uploadTo);
            redirectAttr.addFlashAttribute("successRegister", true);
            return "redirect:/entrance";
        } catch (UserException e) {
            throw new UserRegisterException(e.getMessage(), e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModifyResponseMessage<UserResponseDto>> modify(@PathVariable Long id,
                                                                   @RequestBody UserUpdateDto userUpdateDto,
                                                                   @SessionInfo UserSession userSession,
                                                                   @UploadedFile UploadTo uploadTo) {
        try {
            UserResponseDto user = userService.modify(id, userUpdateDto, userSession.getDto(), uploadTo);
            loginSessionManager.setLoginSession(user);
            ModifyResponseMessage<UserResponseDto> message = ModifyResponseMessage.of(user, UPDATE_SUCCESS_MESSAGE);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (UserException e) {
            ModifyResponseMessage<UserResponseDto> message = ModifyResponseMessage.of(null, e.getMessage());
            return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
        }
    }

    @DeleteMapping("/{id}")
    public String resign(@PathVariable Long id, @SessionInfo UserSession userSession) {
        userService.delete(id, userSession.getDto());
        loginSessionManager.clearSession();
        return "redirect:/";
    }
}
