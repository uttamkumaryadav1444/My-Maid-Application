package MaidRepository.maid.service;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.Language;
import MaidRepository.maid.model.User;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {
    // User methods
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);
    UserResponseDTO getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO request);
    void deleteUser(Long id);
    UserResponseDTO updateProfilePhoto(Long userId, String photoUrl);
    UserResponseDTO deleteProfilePhoto(Long userId) throws Exception;
    UserResponseDTO getUserByMobile(String mobile);
    UserResponseDTO getUserByEmail(String email);
    List<UserResponseDTO> searchUsersByName(String name);
    List<UserResponseDTO> getVerifiedUsers();
    List<UserResponseDTO> getUsersByCity(String city);

    // ✅ NEW: Language methods
    UserResponseDTO updateUserLanguages(String mobile, Set<Language> languages);

    // ✅ NEW: Subscription check methods
    boolean isUserSubscribed(Long userId);
    boolean isUserSubscribed(String mobile);

    // Maid search methods
    List<MaidResponseDTO> getAllAvailableMaids(String mobile);
    List<MaidResponseDTO> searchMaids(MaidSearchRequestDTO request, String mobile);
    UserResponseDTO updateUserByMobile(String mobile, UserUpdateRequestDTO request);
    Map<String, Object> searchMaidsWithCriteria(User user, MaidSearchCriteriaDTO criteria);
}