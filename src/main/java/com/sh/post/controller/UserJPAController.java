package com.sh.post.controller;

import com.sh.post.bean.Post;
import com.sh.post.bean.User;
import com.sh.post.dao.UserDaoService;
import com.sh.post.exception.UserNotFoundException;
import com.sh.post.repository.PostRepository;
import com.sh.post.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/jpa")
@RequiredArgsConstructor
public class UserJPAController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // /jpa/users
    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
        /*
            리스트 형태로 반환하면 유연성 떨어짐. 만약 전체사용자수와 같이 출력해야 한다고하면 수정 어려움
            응답 데이터에 맞는 객체 만들고 그 일부로서 유저 리스트 넣을것.
            여기선 Service 계층이 없지만 총 사용자수를 넣는다면 유저리스트와 사용자수 쿼리를 트랜잭션(query니까 read-only로 하면 될 듯) 안에서 처리 한 후 DTO에 넣어서 반환하도록 해서
            Controller가 entity에 대해 모르도록 하는게 나을지도? 
         */
    }

    // /jpa/users/{id}
    @GetMapping("/users/{id}")
    public ResponseEntity retrieveUserById(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()) {
            throw new UserNotFoundException("id -" + id);
        }
        EntityModel<User> userEntityModel = EntityModel.of(user.get());

        WebMvcLinkBuilder linkBuilder = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        userEntityModel.add(linkBuilder.withRel("all-users")); // all-users -> http://localhost:8088/users
        return ResponseEntity.ok(userEntityModel);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable int id) {
        userRepository.deleteById(id);
    }

    // jpa/users
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);

        // 현재 요청 기반 URI 구성 (1번 방법)
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        // WebMvcLinkBuilder를 이용한 URI 구성 (2번 방법) 간단하게 uri만 반환하는 경우 1번이 나을수 있더라도 성능상에 큰 차이가 없다면 오히려 2번으로 통일하는게 낫지 않을까?
        URI uri = linkTo(methodOn(this.getClass()).retrieveUserById(savedUser.getId())).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/users/{id}/posts")
    public List<Post> retrieveAllPostsByUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("id -" + id);
        }
        return user.get().getPosts();
    }

    @PostMapping("/users/{id}/posts")
    public ResponseEntity createPost(@PathVariable int id, @RequestBody Post post) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("id=" + id);
        }
        User user = userOptional.get(); // Service단에서 실행되는게 더 좋아보이는 로직
        post.setUser(user); // 
        /*
            연관관계 편의 메서드로 user에도 add post 필요하지 않나? 
            그러면 실제로 할 때 PostCreateRequest 객체로 받아와서 service로 넘기면 service가 post(dto to entity)로 변환할텐데
            이 때 연관관계 편의메서드가 변환 메서드 내부에서 쓰이는 형식이 되는건가?
            builder 패턴이 아닌 다른 방식이 강제되나? 나중에 확인 필요
         */
        postRepository.save(post);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }
    // 강의 마무리
}
