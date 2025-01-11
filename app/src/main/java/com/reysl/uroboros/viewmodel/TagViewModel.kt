package com.reysl.uroboros.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.reysl.uroboros.components.MainApplication
import com.reysl.uroboros.data.Tag


class TagViewModel : ViewModel() {
    val tagDao = MainApplication.tagDatabase.getTagDao()
    val tagList: LiveData<List<Tag>> = tagDao.getAllTags()

}