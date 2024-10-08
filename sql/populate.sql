insert into TOOL_TYPE (TYPE_NAME, DAILY_CHARGE, WEEKDAY, WEEKEND, HOLIDAY)
values ('Chainsaw', 1.49, true, false, true),
       ('Ladder', 1.99, true, true, false),
       ('Jackhammer', 2.99, true, false, false);

insert into TOOL_BRAND(BRAND_NAME)
values ('Stihl'),
       ('Werner'),
       ('DeWalt'),
       ('Ridgid');

insert into TOOL(tool_code, type_key, brand_key)
values ('CHNS',
        (SELECT TYPE_KEY FROM TOOL_TYPE WHERE TYPE_NAME = 'Chainsaw'),
        (SELECT BRAND_KEY FROM TOOL_BRAND WHERE BRAND_NAME = 'Stihl')),
       ('LADW',
        (SELECT TYPE_KEY FROM TOOL_TYPE WHERE TYPE_NAME = 'Ladder'),
        (SELECT BRAND_KEY FROM TOOL_BRAND WHERE BRAND_NAME = 'Werner')),
       ('JAKD',
        (SELECT TYPE_KEY FROM TOOL_TYPE WHERE TYPE_NAME = 'Jackhammer'),
        (SELECT BRAND_KEY FROM TOOL_BRAND WHERE BRAND_NAME = 'DeWalt')),
       ('JAKR',
        (SELECT TYPE_KEY FROM TOOL_TYPE WHERE TYPE_NAME = 'Jackhammer'),
        (SELECT BRAND_KEY FROM TOOL_BRAND WHERE BRAND_NAME = 'Ridgid'));
