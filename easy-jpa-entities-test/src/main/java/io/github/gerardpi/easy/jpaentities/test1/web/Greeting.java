package io.github.gerardpi.easy.jpaentities.test1.web;

import java.time.LocalDate;

public class Greeting {

	private final long id;
	private final String content;
	private final LocalDate date;

	public Greeting(long id, String content, LocalDate date) {
		this.id = id;
		this.content = content;
		this.date = date;
	}
	public LocalDate getDate() {
		return date;
	}

	public long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}
}
