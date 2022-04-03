package br.edu.utfpr.emarketplace.service.impl;

import br.edu.utfpr.emarketplace.enumeration.Status;
import br.edu.utfpr.emarketplace.model.Anuncio;
import br.edu.utfpr.emarketplace.model.ImagemAnuncio;
import br.edu.utfpr.emarketplace.model.dto.ImagemAnuncioSalvarDto;
import br.edu.utfpr.emarketplace.repository.AnuncioRepository;
import br.edu.utfpr.emarketplace.service.AnuncioService;
import br.edu.utfpr.emarketplace.service.UsuarioService;
import br.edu.utfpr.emarketplace.service.amazonS3Bucket.AmazonS3BucketServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import static br.edu.utfpr.emarketplace.service.utils.ListUtils.isNullOrEmpty;

@RequiredArgsConstructor
@Service
public class AnuncioServiceImpl extends CrudServiceImpl<Anuncio, Long> implements AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final UsuarioService usuarioService;
    private final AmazonS3BucketServiceImpl amazonS3BucketService;
    private List<String> imagensParaExcluir;
    private List<ImagemAnuncioSalvarDto> imagensDataParaSalvar;

    @Override
    public JpaRepository<Anuncio, Long> getRepository() {
        return anuncioRepository;
    }

    @Override
    public void valid(Anuncio entity) {

    }

    @Override
    public void preSave(Anuncio anuncio) {
        updateImagesAws(anuncio);
    }

    @Override
    public Anuncio salvar(Anuncio anuncio) throws Exception {
        if (anuncio.getId() == null) {
            anuncio.setDataPublicacao(LocalDate.now());
            anuncio.setStatus(Status.DISPONIVEL);
            anuncio.setUsuarioOrigem(usuarioService.getUsuarioLogado());
        }

        return save(anuncio);
    }

    @Override
    public void postSave(Anuncio anuncio) {
        deleteOldImages();
        saveNewImages(anuncio);
    }

    @Override
    public void excluir(Long id) {
        delete(id);
    }

    @Override
    public List<Anuncio> listarTodos() {
        return null;
    }

    private void updateImagesAws(Anuncio anuncio) {
        imagensParaExcluir = null;
        imagensDataParaSalvar = null;
        var imagens = anuncio.getImagens().stream()
                .map(ImagemAnuncio::getUrl)
                .collect(Collectors.toList());
        if (anuncio.getId() != null) {
            imagensParaExcluir = getImagesDeleted(imagens, anuncio.getId());
        }
        imagensDataParaSalvar = getImagensDataParaSalvar(anuncio.getImagens());
        anuncio.getImagens().removeAll(anuncio.getImagens().stream()// todo serve para remover as imagens em base 64, pois já foram salvas e o caminho está na lista
                .filter(imagemAnuncio -> imagemAnuncio.getUrl().startsWith("data")).toList());
    }

    private List<ImagemAnuncioSalvarDto> getImagensDataParaSalvar(List<ImagemAnuncio> imagens) {
        return imagens.stream().filter(imagemAnuncio -> imagemAnuncio.getUrl().startsWith("data"))
                .map(ImagemAnuncioSalvarDto::new)
                .collect(Collectors.toList());
    }

    private void deleteOldImages() {
        if (!isNullOrEmpty(imagensParaExcluir)) {
            imagensParaExcluir.forEach(imagem -> amazonS3BucketService.deleteFileFromBucket(imagem.substring(60, 100), false));
        }
    }

    private void saveNewImages(Anuncio anuncio) {
        if (!isNullOrEmpty(imagensDataParaSalvar)) {
            imagensDataParaSalvar.forEach(imagem -> {
                try {
                    var file = new File(UUID.randomUUID() + ".png");
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagem.getImageData()));
                    ImageIO.write(bufferedImage, "png", file);
                    anuncio.getImagens().add(ImagemAnuncio.builder()
                            .url(amazonS3BucketService.uploadFile(file, false))
                            .nome(imagem.getNome())
                            .build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            anuncioRepository.save(anuncio);
        }
    }

    private List<String> getImagesDeleted(List<String> imagens, Long id) {
        var imagensDoBanco = anuncioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Anuncio não encontrado."))
                .getImagens().stream()
                .map(ImagemAnuncio::getUrl)
                .toList();
        return imagensDoBanco.stream()
                .filter(imagemBanco -> !imagens.contains(imagemBanco) && !imagemBanco.startsWith("data"))
                .collect(Collectors.toList());
    }
}
