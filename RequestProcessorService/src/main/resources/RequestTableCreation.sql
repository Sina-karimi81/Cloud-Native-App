create table if not exists Request (
   ID serial primary key,
   Email varchar(255) not null unique,
   Status varchar(255) not null,
   image_caption varchar(255),
   new_image_url varchar(255)
);