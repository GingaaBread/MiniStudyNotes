package com.gingaabread.ministudynotes.controller

import com.gingaabread.ministudynotes.data.User
import com.gingaabread.ministudynotes.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 *  The [UserController] is used to react to the REST endpoints.
 *  Note that the request is prefixed with "api/v1/users"
 *  At the moment, the controller can react to the following scenarios:
 *  - GET username: Retrieve the user with the specified username (or 404 if not found)
 *  - DELETE username: Delete the user with the specified username (or 404 if not found)
 *  - POST username email: Create a new, unique user (or 400 if taken)
 */
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    @Autowired
    val userRepository: UserRepository
) {

    // Testing Purposes

    @GetMapping
    fun getCount(): Int {
        return userRepository.findAll().count()
    }

    @DeleteMapping("/all")
    fun deleteAll() {
        userRepository.deleteAll()
    }

    // Actual

    /**
     *  Retrieves the user with the specified [username] if the username exists
     *
     *  @param username - the name of the user that should be retrieved
     *
     *  @return 200 OK - if the user exists and has been returned;
     *          404 NOT FOUND - if the specified username does not exist
     */
    @GetMapping("/{username}")
    fun getByUsername(@PathVariable("username") username: String) : ResponseEntity<User> {
        val res = userRepository.findByUsername(username)

        return if (res == null) {
            ResponseEntity
                .notFound()
                .build()
        } else {
            ResponseEntity
                .ok()
                .body(res)
        }
    }

    /**
     *  Creates a new user with the specified [username] and [email] if neither the username nor email already exists.
     *
     *  @param username - the unique name of the user
     *  @param email - the unique email of the user
     *
     *  @return 200 OK - if the username and email are unique and the new user has been therefore created;
     *          400 BAD REQUEST - if either (or both) the username or email already exist
     */
    @PostMapping("/{username}/{email}")
    fun postUser(@PathVariable("username") username: String,
                 @PathVariable("email") email: String): ResponseEntity<String> {
        // First, check for illegal parameters and in that case send a 400
        return if (userRepository.existsByEmail(email) && userRepository.existsByUsername(username)) {
            ResponseEntity
                .badRequest()
                .body("Username and Email already taken.")
        } else if (userRepository.existsByUsername(username)) {
            ResponseEntity
                .badRequest()
                .body("Username already taken.")
        } else if (userRepository.existsByEmail(email)) {
            ResponseEntity
                .badRequest()
                .body("Email already taken.")
        } else {
            // Create the new user
            val user = User(username = username, email = email)

            // Add it to the repository and send a 200
            userRepository.insert(user)
            ResponseEntity
                .ok()
                .build()
        }
    }

    /**
     *  Deletes the user with the specified [username]
     *
     *  @param username the name of the user that should be deleted
     *
     *  @return 200 OK - if the specified username exists and has been deleted;
     *          404 NOT FOUND - if the specified username does not exist
     */
    @DeleteMapping("/{username}")
    fun deleteUserByUsername(@PathVariable("username") username: String) : ResponseEntity<String> {
        val res = userRepository.findByUsername(username)

        // If the username does not exist, send a 404
        return if (res == null) {
            ResponseEntity
                .status(404)
                .body("Username $username does not exist")
        }
        else {
            // If the user exists, delete it and send a 200
            userRepository.delete(res)

            ResponseEntity
                .ok()
                .build()
        }
    }

}