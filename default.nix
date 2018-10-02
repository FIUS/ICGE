with import <nixpkgs> {};

buildEnv {
  name = "mariojvk-env";
  paths = [ jdk ];
}
