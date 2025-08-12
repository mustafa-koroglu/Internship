-- Create students database if not exists
CREATE DATABASE IF NOT EXISTS students;

-- Use students database
USE students;

-- Create students table
CREATE TABLE IF NOT EXISTS students (
    id UInt64,
    name String,
    surname String,
    number String
) ENGINE = MergeTree()
ORDER BY id;
