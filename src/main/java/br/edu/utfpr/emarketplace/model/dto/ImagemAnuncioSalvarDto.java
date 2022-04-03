package br.edu.utfpr.emarketplace.model.dto;

import br.edu.utfpr.emarketplace.model.ImagemAnuncio;
import lombok.Getter;
import lombok.Setter;

import static br.edu.utfpr.emarketplace.service.utils.ImageUtils.converterDataToByte;

@Getter
@Setter
public class ImagemAnuncioSalvarDto {
    private byte[] imageData;
    private String nome;

    public ImagemAnuncioSalvarDto(ImagemAnuncio imagemAnuncio) {
        this.imageData = converterDataToByte(imagemAnuncio.getUrl());
        this.nome = imagemAnuncio.getNome();
    }
}
