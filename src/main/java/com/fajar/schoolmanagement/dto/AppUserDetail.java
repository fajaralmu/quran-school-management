package com.fajar.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fajar.schoolmanagement.entity.User;

public class AppUserDetail implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = -947916122342184508L;
	private final User user;
	
	public AppUserDetail(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { 
		return new ArrayList<GrantedAuthority>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -6356413082111153328L;

			{
				add(new SimpleGrantedAuthority("USER"));
			}
		};
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
