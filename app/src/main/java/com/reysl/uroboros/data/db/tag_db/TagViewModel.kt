package com.reysl.uroboros.data.db.tag_db

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reysl.uroboros.MainApplication
import com.reysl.uroboros.data.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TagViewModel : ViewModel() {
    val tagDao = MainApplication.tagDatabase.getTagDao()
    val tagList: LiveData<List<Tag>> = tagDao.getAllTags()

    fun addTag(tag: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingTagCount = tagDao.getTagCount(tag)
            if (existingTagCount == 0) {
                tagDao.addTag(
                    Tag(
                        tag = tag
                    )
                )
            }
        }
    }

}