create table if not exists posts(
  id bigserial primary key,
  title varchar(100),
  text varchar(1000),
  tags varchar(100),
  image_path varchar(100),
  likes_count integer not null
);

create table if not exists comments(
  id bigserial primary key,
  post_id bigserial REFERENCES posts (id),
  text varchar(1000)
);