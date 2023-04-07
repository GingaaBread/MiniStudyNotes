package com.gingaabread.ministudynotes.controller

import com.gingaabread.ministudynotes.data.StudyNote
import com.gingaabread.ministudynotes.data.Subject
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
     *  Creates a new [Subject] with the specified [subjectName] for the user with the specified [username]
     *  If the user does not exist or the subject name already exists, this method sends an error, instead.
     *
     *  @param username - the name of the user that should receive the new subject
     *  @param subjectName - the name of the subject that should be created
     *
     *  @return 200 OK - if the user exists, the subject name is unique, and the subject has been created
     *          400 BAD REQUEST - if either the user does not exist or the subject name already exists
     */
    @PostMapping("/{username}/subjects/{subjectName}")
    fun createNewSubject(@PathVariable("username") username: String,
                         @PathVariable("subjectName") subjectName: String) : ResponseEntity<String> {
        // Check if the specified user does not exist
        return if (userRepository.findByUsername(username) == null) {
            ResponseEntity
                .badRequest()
                .body("Username does not exist.")
            // Note that the following not null cast !! is okay as we are checking for null above
        } else if (userRepository.findByUsername(username)!!.subjects.any { it.name == subjectName }) {
            // If a subject with the given name already exists, return an error
            ResponseEntity
                .badRequest()
                .body("Subject name already exists.")
        } else {
            // If everything is okay, create the new subject and send a 200
            val editedUser = userRepository.findByUsername(username)!!
            editedUser.subjects += Subject(name = subjectName)
            userRepository.save(editedUser)

            ResponseEntity
                .ok()
                .build()
        }
    }

    /**
     *  Deletes the [Subject] with the specified [subjectName] of the User with the specified [username]
     *  If the user or subject does not exist, sends an error, instead.
     *
     *  @param username - the name of the user whose subject should be deleted
     *  @param subjectName - the name of the subject that should be deleted
     *
     *  @return 200 OK - if the username and subject exists and the subject has been deleted
     *          400 BAD REQUEST - if the username or subject does not exist
     */
    @DeleteMapping("/{username}/subjects/{subjectName}")
    fun deleteSubject(@PathVariable("username") username: String,
                         @PathVariable("subjectName") subjectName: String) : ResponseEntity<String> {
        // Check if the specified user does not exist
        return if (userRepository.findByUsername(username) == null) {
            ResponseEntity
                .badRequest()
                .body("Username does not exist.")
            // Note that the following not null cast !! is okay as we are checking for null above
        } else if (userRepository.findByUsername(username)!!.subjects.none { it.name == subjectName }) {
            // If the user does not have a subject with the specified name send an error
            ResponseEntity
                .badRequest()
                .body("Subject does not exist.")
        } else {

            val editedUser = userRepository.findByUsername(username)!!
            val subjectToRemove = editedUser.subjects.find { it.name == subjectName }!!

            // If everything is okay, delete the subject and send a 200
            editedUser.subjects -= subjectToRemove
            userRepository.save(editedUser)

            ResponseEntity
                .ok()
                .build()
        }
    }

    /**
     *  Changes the subject name of the specified subject.
     *  If the original subject or username does not exist, sends an error, instead.
     *
     *  @param username - the name of the user whose subject should be renamed
     *  @param subjectName - the original name of the subject that should be renamed to [newSubjectName]
     *  @param newSubjectName - the new name replacing the original [subjectName]
     *
     *  @return 200 OK - if the username and subject exists and the subject has been renamed
     *          400 BAD REQUEST - if the user or subject does not exist or the new subject name already exists
     */
    @PutMapping("/{username}/subjects/{subjectName}/{newSubjectName}")
    fun renameSubject(@PathVariable("username") username: String,
                      @PathVariable("subjectName") subjectName: String,
                      @PathVariable("newSubjectName") newSubjectName: String) : ResponseEntity<String> {
        // Check if the specified user does not exist
        return if (userRepository.findByUsername(username) == null) {
            ResponseEntity
                .badRequest()
                .body("Username does not exist.")
            // Note that the following not null cast !! is okay as we are checking for null above
        } else if (userRepository.findByUsername(username)!!.subjects.none { it.name == subjectName }) {
            // If the user does not have a subject with the specified name send an error
            ResponseEntity
                .badRequest()
                .body("Subject does not exist.")
        } else if (userRepository.findByUsername(username)!!.subjects.any { it.name == newSubjectName }) {
            // If a subject with the new given name already exists, return an error
            ResponseEntity
                .badRequest()
                .body("New subject name already exists.")
        } else {
            val editedUser = userRepository.findByUsername(username)!!
            val subjectToEdit = editedUser.subjects.find { it.name == subjectName }!!
            subjectToEdit.name = newSubjectName

            // If everything is okay, edit the subject and send a 200
            userRepository.save(editedUser)

            ResponseEntity
                .ok()
                .build()
        }
    }

    /**
     *  Retrieves the subjects of the user with the specified [username]
     *
     *  @param username - the name of the user whose subjects should be returned
     *
     *  @return 200 OK + Subjects - if the user exists
     *          400 BAD REQUEST - if the user does not exist
     */
    @GetMapping("/{username}/subjects")
    fun getSubjects(@PathVariable("username") username: String) : ResponseEntity<List<Subject>> {
        // Check if the specified user does not exist
        return if (userRepository.findByUsername(username) == null) {
            ResponseEntity
                .badRequest()
                .build()
            // Note that the following not null cast !! is okay as we are checking for null above
        } else {
            ResponseEntity
                .ok()
                .body(userRepository.findByUsername(username)!!.subjects)
        }
    }

    @PostMapping("/{username}/subjects/{subjectName}/notes")
    fun createStudyNote(@PathVariable("username") username: String,
                        @PathVariable("subjectName") subjectName: String,
                        @RequestBody note: StudyNote) : ResponseEntity<String> {
        // Check if the specified user does not exist
        return if (!userRepository.existsByUsername(username)) {
            ResponseEntity
                .badRequest()
                .body("Username does not exist")
            // Note that the following not null cast !! is okay as we are checking for null above
        } else if (userRepository.findByUsername(username)!!.subjects.none { it.name == subjectName }) {
            ResponseEntity
                .badRequest()
                .body("Username does not have a subject with the name '$subjectName'")
        } else {
            val user = userRepository.findByUsername(username)!!
            val subject = user.subjects.find { it.name == subjectName }!!
            subject.notes += note

            userRepository.save(user)

            ResponseEntity
                .ok()
                .build()
        }
    }

    @GetMapping("/{username}/subjects/{subjectName}/notes")
    fun getStudyNotes(@PathVariable("username") username: String,
                      @PathVariable("subjectName") subjectName: String) : ResponseEntity<List<StudyNote>> {
        // Check if the specified user does not exist
        return if (!userRepository.existsByUsername(username)) {
            ResponseEntity
                .badRequest()
                .build()
            // Note that the following not null cast !! is okay as we are checking for null above
        } else if (userRepository.findByUsername(username)!!.subjects.none { it.name == subjectName }) {
            ResponseEntity
                .badRequest()
                .build()
        } else {
            val user = userRepository.findByUsername(username)!!
            val subject = user.subjects.find { it.name == subjectName }!!

            ResponseEntity
                .ok()
                .body(subject.notes)
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