enum FormType { login, register, editprofile }

class EmailValidator {
  static String validate(String value) {
    if (value.isEmpty) {
      return "Dit veld mag niet leeg zijn";
    }
    if (value.length < 6) {
      return "Ongeldig email";
    }
    if (!value.contains('@')) {
      return "Ongeldig email, moet @ bevatten";
    }

    if (value.substring(value.length - 1) == ' ') {
      return "Mag een spatie aan het einde bevatten";
    }
    if (!RegExp(
            r'^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$')
        .hasMatch(value)) {
      return "Ongeldig email format";
    }
    return null;
  }

  static String spaceCheck(value) {
    if (value.substring(value.length - 1) == ' ' && !value.isEmpty) {
      return spaceCheck(value.substring(0, value.length -1));
    } else {
      return value;
    }

  }
}

class PasswordValidator {
  static String validate(String value) {
    if (value.isEmpty) {
      return "Dit veld mag niet leeg zijn";
    }

    if (value.length < 6) {
      return "Wachtwoord is te kort";
    }
    return  null;
  }
}

class NameValidator {
  static String validate(String value) {
    return value.isEmpty ? "Dit veld mag niet leeg zijn" : null;
  }
}
