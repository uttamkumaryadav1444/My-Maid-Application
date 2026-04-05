package MaidRepository.maid.service;

import MaidRepository.maid.model.User;
import MaidRepository.maid.model.Maid;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.repository.MaidRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MaidRepository maidRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);

        // Check if user exists in users table
        User user = userRepository.findByMobile(username).orElse(null);
        if (user != null) {
            log.info("Found user with mobile: {}", username);
            return new org.springframework.security.core.userdetails.User(
                    user.getMobile(),
                    user.getPassword(),
                    user.getAccountStatus() == User.AccountStatus.ACTIVE,
                    true,
                    true,
                    true,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }

        // Check if user exists in maid table
        Maid maid = maidRepository.findByMobile(username).orElse(null);
        if (maid != null) {
            log.info("Found maid with mobile: {}", username);
            return new org.springframework.security.core.userdetails.User(
                    maid.getMobile(),
                    maid.getPassword(),
                    maid.getStatus() == Maid.MaidStatus.ACTIVE,
                    true,
                    true,
                    true,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_MAID"))
            );
        }

        log.error("User not found with mobile: {}", username);
        throw new UsernameNotFoundException("User not found with mobile: " + username);
    }
}