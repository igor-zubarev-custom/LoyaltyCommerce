drop table loyalty_transaction;
drop table dynamusic_user_loyalty;
drop table dcsp_loyalty_points;

commit work;

CREATE TABLE loyalty_transaction (
        loyalty_id              VARCHAR(32)     not null,
        amount                  INTEGER         not null,
        description             LONG VARCHAR    null,
        transaction_date        TIMESTAMP       not null,
        user_id                 VARCHAR(32)     not null,        
        primary key(loyalty_id)
);

CREATE TABLE dynamusic_user_loyalty (
		user_id                 VARCHAR(32)     not null references dps_user(id),
		loyalty_id              VARCHAR(32)     not null references loyalty_transaction(loyalty_id),
		seq_key                 INTEGER         not null,
		primary key(user_id,loyalty_id)
);

CREATE TABLE dcsp_loyalty_points (
		payment_group_id           VARCHAR(32)     not null references dcspp_pay_group(payment_group_id),		
		user_id       		       VARCHAR(32)     not null,
		primary key(payment_group_id)
);

commit work;


