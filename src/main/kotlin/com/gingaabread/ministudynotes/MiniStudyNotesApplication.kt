package com.gingaabread.ministudynotes

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MiniStudyNotesApplication

fun main(args: Array<String>) {
	runApplication<MiniStudyNotesApplication>(*args) {
		setBannerMode(Banner.Mode.OFF)
	}
}
