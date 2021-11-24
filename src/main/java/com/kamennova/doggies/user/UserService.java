package com.kamennova.doggies.user;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.kamennova.doggies.KeyStorageConfig;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeyStorageConfig securityConfig;

    private Aead aead = AeadFactory.getPrimitive(KeysetHandle.generateNew(
            AeadKeyTemplates.AES256_GCM));

    public UserService() throws GeneralSecurityException {
        AeadConfig.register();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public String validate(String email, String password) {
        Optional<User> existingEmail = userRepository.findByEmail(email);

        if (existingEmail.isPresent()) {
            return "Акаунт з таким емейлом уже існує";
        } else if (!EmailValidator.getInstance().isValid(email)) {
            return "Емейл невалідний";
        } else if (password.length() < 8) {
            return "Пароль має містити хоча б 8 символів";
        } else if (isPasswordCommon(password)) {
            return "Пароль занадто поширений";
        }

        return "";
    }

    private static boolean isPasswordCommon(String password) {
        String abs = Paths.get(".").toAbsolutePath().normalize().toString();

        try (Stream<String> stream = Files.lines(Paths.get(abs + "/src/main/resources/common.txt"))) {
            return stream.anyMatch(str -> str.equals(password));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public byte[] encryptUserAddress(String address) throws GeneralSecurityException {
        return aead.encrypt(address.getBytes(), securityConfig.key.getBytes());
    }

    public String decodeUserAddress(byte[] encrypted) throws GeneralSecurityException {
        return new String(aead.decrypt(encrypted, securityConfig.key.getBytes()));
    }
}
