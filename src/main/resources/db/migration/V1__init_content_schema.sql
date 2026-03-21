create table content_items (
                               id bigserial primary key,
                               content_type varchar(30) not null,
                               title varchar(150),
                               thumbnail_image_url varchar(500),
                               is_visible boolean not null default true,
                               display_order int not null default 0,
                               created_at timestamp not null default current_timestamp,
                               updated_at timestamp not null default current_timestamp
);

create table pub_contents (
                              id bigserial primary key,
                              content_item_id bigint not null unique,
                              college_name jsonb not null,
                              is_name_confirmed boolean not null default false,
                              created_at timestamp not null default current_timestamp,
                              updated_at timestamp not null default current_timestamp,
                              constraint fk_pub_contents_content_item
                                  foreign key (content_item_id) references content_items(id)
);

create table food_truck_contents (
                                     id bigserial primary key,
                                     content_item_id bigint not null unique,
                                     name jsonb not null,
                                     short_description jsonb,
                                     created_at timestamp not null default current_timestamp,
                                     updated_at timestamp not null default current_timestamp,
                                     constraint fk_food_truck_contents_content_item
                                         foreign key (content_item_id) references content_items(id)
);

create table md_contents (
                             id bigserial primary key,
                             content_item_id bigint not null unique,
                             name jsonb not null,
                             price int not null,
                             product_description jsonb,
                             detail_description jsonb,
                             detail_image_url varchar(500),
                             is_sold_out boolean not null default false,
                             created_at timestamp not null default current_timestamp,
                             updated_at timestamp not null default current_timestamp,
                             constraint fk_md_contents_content_item
                                 foreign key (content_item_id) references content_items(id)
);

create table md_option_groups (
                                  id bigserial primary key,
                                  md_content_id bigint not null,
                                  name jsonb not null,
                                  display_order int not null default 0,
                                  created_at timestamp not null default current_timestamp,
                                  updated_at timestamp not null default current_timestamp,
                                  constraint fk_md_option_groups_md_content
                                      foreign key (md_content_id) references md_contents(id)
);

create table md_option_values (
                                  id bigserial primary key,
                                  option_group_id bigint not null,
                                  value_name jsonb not null,
                                  extra_price int not null default 0,
                                  is_sold_out boolean not null default false,
                                  display_order int not null default 0,
                                  created_at timestamp not null default current_timestamp,
                                  updated_at timestamp not null default current_timestamp,
                                  constraint fk_md_option_values_option_group
                                      foreign key (option_group_id) references md_option_groups(id)
);

create table notices (
                         id bigserial primary key,
                         title jsonb not null,
                         content jsonb not null,
                         posted_at timestamp not null,
                         is_pinned boolean not null default false,
                         is_visible boolean not null default true,
                         created_at timestamp not null default current_timestamp,
                         updated_at timestamp not null default current_timestamp
);