package com._9.inspect_pro.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com._9.inspect_pro.model.ProfileType;
import com._9.inspect_pro.model.User;
import com._9.inspect_pro.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        Collection<GrantedAuthority> authorities = getAuthorities(user);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                authorities);

    }

    public Collection<GrantedAuthority> getAuthorities(User user) {

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        boolean hasVPProfile = user.getProfiles().stream()
                .anyMatch(profile -> profile.getType() == ProfileType.VERIFIED_PROFESSIONAL);

        if (hasVPProfile) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VP"));
        }

        return authorities;
    }

    

}
