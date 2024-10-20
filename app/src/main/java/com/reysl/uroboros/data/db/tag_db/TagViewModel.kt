package com.reysl.uroboros.data.db.tag_db

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.reysl.uroboros.MainApplication
import com.reysl.uroboros.data.Tag


class TagViewModel : ViewModel() {
    val tagDao = MainApplication.tagDatabase.getTagDao()
    val tagList: LiveData<List<Tag>> = tagDao.getAllTags()

}