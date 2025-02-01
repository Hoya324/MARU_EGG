package mju.iphak.maru_egg.auth.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.user.domain.User;
import mju.iphak.maru_egg.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("[Error] 이미 존재하는 이메일입니다.");
        }
        User user = User.of(email, password);
        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
        log.info("회원 저장 성공");
    }

    public void signIn(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(NOT_FOUND_USER.getMessage(), email)));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("[Error] 로그인에 실패했습니다.");
        }
    }
}
