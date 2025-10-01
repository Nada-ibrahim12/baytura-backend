package org.os.bayturabackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "profile_picture_url")
    @Builder.Default
    private String profilePictureUrl = "https://videos.openai.com/vg-assets/assets%2Ftask_01k4v15jwsfhgr10pac5rk4e1r%2F1757548901_img_1.webp?st=2025-10-01T19%3A55%3A16Z&se=2025-10-07T20%3A55%3A16Z&sks=b&skt=2025-10-01T19%3A55%3A16Z&ske=2025-10-07T20%3A55%3A16Z&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skoid=b4ab33b8-2ad4-40af-8ed0-a2b350b6603c&skv=2019-02-02&sv=2018-11-09&sr=b&sp=r&spr=https%2Chttp&sig=IA7Ejbrk1yQSdAGEZfpc%2FOVkYygEBIVcddxK3vEQZeg%3D&az=oaivgprodscus" ;

    @Column(name = "profile_picture_id")
    private String profilePictureId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "last_credentials_update")
    private Instant lastCredentialsUpdate = Instant.now();  // ? for tracking when the email or password is updated

    @PrePersist
    public void prePersist() {
        if (lastCredentialsUpdate == null) {
            lastCredentialsUpdate = Instant.now();
        }
    }

    public Long getUserId() {
        return userId;
    }


    public abstract void setRole();

    public abstract String getUserType();


    public String getRealUsername(){
        return this.username;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole().name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}