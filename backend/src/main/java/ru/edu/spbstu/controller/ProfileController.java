package ru.edu.spbstu.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.edu.spbstu.request.UpdateProfilePhotoRequest;
import ru.edu.spbstu.service.ProfileService;

@RestController
@AllArgsConstructor
public class ProfileController {

    private ProfileService profileService;

    @GetMapping("/get_profile_photo")
    public byte[] getProfilePhoto(@RequestParam("login") String login) {
        return profileService.getProfilePhoto(login);
    }

    @PatchMapping("/update_profile_photo")
    public void updateProfilePhoto(@RequestBody UpdateProfilePhotoRequest request) {
        profileService.updateProfilePhoto(request);
    }
}
