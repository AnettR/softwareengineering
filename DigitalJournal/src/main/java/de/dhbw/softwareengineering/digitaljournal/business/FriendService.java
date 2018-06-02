package de.dhbw.softwareengineering.digitaljournal.business;

import de.dhbw.softwareengineering.digitaljournal.domain.Friend;
import de.dhbw.softwareengineering.digitaljournal.domain.form.CreateFriend;
import de.dhbw.softwareengineering.digitaljournal.persistence.FriendRepository;
import de.dhbw.softwareengineering.digitaljournal.util.UUIDGenerator;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class FriendService extends AbstractService{
    private final FriendRepository friendRepository;

    public FriendService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    public List<Friend> findAll(String name){
        List<Friend> friends = friendRepository.findAllByName(name);
        return friends;
    }

    public void save(CreateFriend createFriend, Principal principal, UserService userService){
        ///TODO XSS
        Friend friend = new Friend();
        String friendName = createFriend.getUsername();
        if(userService.exists(friendName)) {
            friend.setId(UUIDGenerator.generateUniqueUUID(friendRepository));
            friend.setName(principal.getName());
            friend.setFriendName(createFriend.getUsername());
            friendRepository.save(friend);
        } else {
            //TODO notify, that this user does not exist
        }
    }
}
