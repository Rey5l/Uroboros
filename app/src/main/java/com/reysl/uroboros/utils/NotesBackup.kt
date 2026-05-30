package com.reysl.uroboros.utils

import com.reysl.uroboros.data.Note
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

object NotesBackup {

    const val VERSION = 1

    data class Backup(
        val notes: List<Note>,
        val tags: List<String>,
    )

    fun toJson(notes: List<Note>, tags: List<String>): String {
        val root = JSONObject()
        root.put("version", VERSION)
        root.put("exportedAt", System.currentTimeMillis())

        val notesArray = JSONArray()
        notes.forEach { note ->
            notesArray.put(
                JSONObject().apply {
                    put("title", note.title)
                    put("description", note.description)
                    put("isFavourite", note.isFavourite)
                    put("tag", note.tag)
                    put("styledText", note.styledText)
                    put("time", note.time.time)
                }
            )
        }
        root.put("notes", notesArray)

        val tagsArray = JSONArray()
        tags.forEach { tagsArray.put(it) }
        root.put("tags", tagsArray)

        return root.toString(2)
    }

    fun fromJson(json: String): Backup {
        val root = JSONObject(json)
        val version = root.getInt("version")
        require(version == VERSION) { "Unsupported backup version: $version" }

        val notes = buildList {
            val notesArray = root.getJSONArray("notes")
            for (index in 0 until notesArray.length()) {
                val item = notesArray.getJSONObject(index)
                add(
                    Note(
                        title = item.getString("title"),
                        description = item.getString("description"),
                        isFavourite = item.getBoolean("isFavourite"),
                        tag = item.getString("tag"),
                        styledText = item.getString("styledText"),
                        time = Date(item.getLong("time")),
                    )
                )
            }
        }

        val tags = buildList {
            val tagsArray = root.getJSONArray("tags")
            for (index in 0 until tagsArray.length()) {
                add(tagsArray.getString(index))
            }
        }

        return Backup(notes = notes, tags = tags)
    }
}
