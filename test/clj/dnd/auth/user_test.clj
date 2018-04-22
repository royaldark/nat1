(ns dnd.auth.user-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [codax.core :as c]
            [dnd.auth.user :as auth-user]
            [dnd.testing.fixtures :as fixt]))

(use-fixtures :once fixt/server-fixture)

(deftest password-hashing
  (testing "Hashing the same string multiple times -> different output hashes"
    (let [n 5
          hashes (set (repeatedly n #(auth-user/hash-password "asdf")))]
      (= n (count hashes))))

  (testing "Right password -> success"
    (let [plaintext "i am a password"
          hashes    (repeatedly 5 #(auth-user/hash-password plaintext))]
      (doseq [hash hashes]
        (is (auth-user/password-matches? plaintext hash)))))

  (testing "Wrong password -> fail"
    (let [plaintext "i am a password"
          hashes    (repeatedly 5 #(auth-user/hash-password plaintext))]
      (doseq [hash hashes]
        (is (not (auth-user/password-matches? "a different password" hash)))))))

(deftest login-by-id!
  (testing "Invalid ID -> err"
    (let [id (auth-user/uuid)]
      (is (thrown? RuntimeException (auth-user/login-by-id! id "nonesuch")))))

  (testing "Valid credentials -> User"
    (let [user {:username "test user"
                :password (auth-user/uuid)
                :email "a@b.com"}
          created (auth-user/create! user)
          authed (auth-user/login-by-id! (:id created) (:password user))
          authed (:user authed)]

      (is (= (select-keys user    [:username :email])
             (select-keys created [:username :email])
             (select-keys authed  [:username :email])))

      (is (not= (:password user) (:password created)))
      (is (not= (:password user) (:password authed)))

      (is (:id created))
      (is (= (:id created) (:id authed))))))
