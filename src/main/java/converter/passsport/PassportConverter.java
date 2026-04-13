package converter.passsport;

import domain.Passport;
import dto.passport.PassportDto;
import converter.Converter;

public class PassportConverter implements Converter<PassportDto, Passport> {
    @Override
    public Passport convert(PassportDto dto) {
        var passport = new Passport();

        passport.setSeries(dto.series());
        passport.setNumber(dto.number());
        passport.setCitizenship(dto.citizenship());
        passport.setIssueDate(dto.issueDate());
        passport.setExpiredDate(dto.expiredDate());

        return passport;
    }
}
