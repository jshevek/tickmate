package de.smasi.tickmate.models;

import android.content.Context;
import android.content.res.Resources;

public class Group {
	String name;
	int id;

	public Group(String name) {
		super();
		this.name = name;
		this.id = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
