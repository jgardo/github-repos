package pl.jgardo.github.repos.controller.mapper.converter;

public interface Converter<Value, DtoFormat> {

	DtoFormat toDtoFormat(Value value);	
}
