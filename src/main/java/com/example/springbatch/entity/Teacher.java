package com.example.springbatch.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Teacher {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	public Teacher(Long id, String name) {
		this.id = id;
		this.name = name;
	}
}
