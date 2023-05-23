package com.ywauran.storyapp.utils

import com.ywauran.storyapp.data.remote.response.*
import java.util.*

const val ERROR_MESSAGE = "Error getting data"

fun generateSuccessLoginResponse(): LoginResponse = LoginResponse(
    error = false,
    message = "success",
    loginResult = LoginResultResponse(
        userId = "user-BUXrdIwvuT6AtdrD",
        name = "ywauran",
        token = "token testing"
    )
)

fun generateErrorLoginResponse(): LoginResponse = LoginResponse(
    error = true,
    message = "Invalid password"
)

fun generateSuccessRegisterResponse(): HandlingResponse = HandlingResponse(
    error = false,
    message = "User created"
)

fun generateErrorRegisterResponse(): HandlingResponse = HandlingResponse(
    error = true,
    message = "Email is already taken"
)

fun generateSuccessDummyListStoryResponse(): List<StoryResponse> {
    return (1..5).map { i ->
        StoryResponse(
            id = "id++$i",
            name = "name++$i",
            description = "description++$i",
            photoUrl = "photo++$i.jpg",
            createdAt = "created++$i"
        )
    }
}

fun generateSuccessDummyCreateStory(): HandlingResponse = HandlingResponse(
    error = false,
    message = "Story created successfully"
)

fun generateErrorDummyCreateStory(): HandlingResponse = HandlingResponse(
    error = false,
    message = "The photo should be readable"
)

fun generateSuccessDummyListStoryLocation(): ListStoryResponse {
    val stories = LinkedList<StoryResponse>()

    for (i in 1..10) {
        stories.add(
            StoryResponse(
                id = "id++$i",
                name = "name++$i",
                description = "description++$i",
                photoUrl = "photo++$i.jpg",
                createdAt = "created++$i",
                lat = 0.998747 + i,
                lon = 124.279551 + i
            )
        )
    }

    return ListStoryResponse(
        error = false,
        message = "Stories fetched successfully",
        listStory = stories
    )
}

fun generateErrorDummyListStoryLocation(): ListStoryResponse = ListStoryResponse(
    error = true,
    message = "Authentication missing",
    listStory = null
)

fun generateSuccessDummyDetailStory(): DetailStoryResponse = DetailStoryResponse(
    error = false,
    message = "Story fetched successfully",
    story = StoryResponse(
        id = "id",
        name = "name",
        description = "description",
        photoUrl = "photo.jpg",
        createdAt = "created"
    )
)

fun generateErrorDummyDetailStory(): DetailStoryResponse = DetailStoryResponse(
    error = true,
    message = "Story not found"
)
