package com.dicoding.submissionintermediate

import com.dicoding.submissionintermediate.data.response.ListStoryItem


object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                "photoUrl + $i",
                "createdAt - $i",
                "name $i",
                "desc $i",
                i.toDouble(),
                i.toString(),
                i.toDouble()
            )
            items.add(quote)
        }
        return items
    }
}