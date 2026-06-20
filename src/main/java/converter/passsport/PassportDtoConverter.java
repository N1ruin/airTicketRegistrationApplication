package converter.passsport;

import domain.Passport;
import dto.passport.PassportDto;
import converter.Converter;

public class PassportDtoConverter implements Converter<Passport, PassportDto> {
    @Override
    public PassportDto convert(Passport passport) {
        return new PassportDto(passport.getFirstName(),
                passport.getLastName(),
                passport.getFatherName(),
                passport.isMale(),
                passport.getSeries(),
                passport.getNumber(),
                passport.getCitizenship(),
                passport.getBirthDate(),
                passport.getIssueDate(),
                passport.getExpiredDate());
    }
}
