(ns dnd.auth.user-test
  (:require [clojure.test :refer [deftest is testing]]
            [dnd.auth.user :as auth-user]))

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
