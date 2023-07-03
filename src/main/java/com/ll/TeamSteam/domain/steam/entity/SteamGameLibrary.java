package com.ll.TeamSteam.domain.steam.entity;

import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.global.baseEntity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class SteamGameLibrary extends BaseEntity{
	@Column(name = "appid")
	private Integer appid;

	@Column(name = "name")
	private String name;

	@Column(name = "image_url")
	private String imageUrl;

	@ManyToOne
	private User user;

	public SteamGameLibrary(Integer appid, String name, String imageUrl) {
		this.appid = appid;
		this.name = name;
		this.imageUrl = imageUrl;
	}

	// Getters and Setters
}