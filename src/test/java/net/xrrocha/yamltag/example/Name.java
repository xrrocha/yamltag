package net.xrrocha.yamltag.example;

public class Name {
	private String firstName;
	private String middleName;
	private String lastName;

	public Name() {
	}

	public Name(String firstName, String middleName, String lastName) {
		setFirstName(firstName);
		setMiddleName(middleName);
		setLastName(lastName);
	}

	public Name(String firstName, String lastName) {
		setFirstName(firstName);
		setLastName(lastName);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Name)) {
			return false;
		}
		Name otherName = (Name) other;
		
		if (firstName != null) {
			if (!firstName.equals(otherName.firstName)) {
				return false;
			}
		} else {
			if (otherName.firstName != null) {
				return false;
			}
		}
		
		if (middleName != null) {
			if (!firstName.equals(otherName.middleName)) {
				return false;
			}
		} else {
			if (otherName.middleName != null) {
				return false;
			}
		}
		
		if (lastName != null) {
			if (!lastName.equals(otherName.lastName)) {
				return false;
			}
		} else {
			if (otherName.lastName != null) {
				return false;
			}
		}
		
		return true;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
