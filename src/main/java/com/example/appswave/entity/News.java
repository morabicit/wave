package com.example.appswave.entity;

import com.example.appswave.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Arabic title is required")
    @Size(max = 200, message = "Arabic title must be less than 200 characters")
    @Column(nullable = false, name = "title_ar")
    private String titleAr;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    @Column(nullable = false)
    private String description;

    @NotBlank(message = "Arabic description is required")
    @Size(max = 1000, message = "Arabic description must be less than 1000 characters")
    @Column(nullable = false, name = "description_ar")
    private String descriptionAr;

    @NotNull(message = "Publish date is required")
    @FutureOrPresent(message = "Publish date must be in the present or future")
    @Column(nullable = false, name = "publish_date")
    private Date publishDate;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;
    @Enumerated
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

}
