package converter.passsport;

import domain.Passport;
import dto.passport.PassportDto;
import converter.Converter;

public class PassportDtoConverter implements Converter<Passport, PassportDto> {
    @Override
    public PassportDto convert(Passport passport) {
        return new PassportDto(passport.getSeries(), passport.getNumber(), passport.getCitizenship(),
                passport.getIssueDate(), passport.getExpiredDate());
    }
}
