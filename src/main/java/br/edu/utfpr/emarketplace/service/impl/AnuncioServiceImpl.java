package br.edu.utfpr.emarketplace.service.impl;

import br.edu.utfpr.emarketplace.enumeration.Operacao;
import br.edu.utfpr.emarketplace.enumeration.Status;
import br.edu.utfpr.emarketplace.exception.RecursoNaoPermitidoException;
import br.edu.utfpr.emarketplace.model.Anuncio;
import br.edu.utfpr.emarketplace.model.ImagemAnuncio;
import br.edu.utfpr.emarketplace.model.Usuario;
import br.edu.utfpr.emarketplace.model.dto.ImagemAnuncioSalvarDto;
import br.edu.utfpr.emarketplace.repository.AnuncioRepository;
import br.edu.utfpr.emarketplace.repository.ImagemAnuncioRepository;
import br.edu.utfpr.emarketplace.repository.criteria.params.AnuncioFilter;
import br.edu.utfpr.emarketplace.service.AnuncioService;
import br.edu.utfpr.emarketplace.service.UsuarioService;
import br.edu.utfpr.emarketplace.service.amazonS3Bucket.AmazonS3BucketService;
import br.edu.utfpr.emarketplace.service.email.EnvioEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static br.edu.utfpr.emarketplace.service.utils.ListUtils.isNullOrEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Service
public class AnuncioServiceImpl extends CrudServiceImpl<Anuncio, Long> implements AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final UsuarioService usuarioService;
    private final EnvioEmailService envioEmailService;
    private final AmazonS3BucketService amazonS3BucketService;
    private final ImagemAnuncioRepository imagemAnuncioRepository;
    private List<ImagemAnuncio> imagensParaExcluir;
    private List<ImagemAnuncioSalvarDto> imagensDataParaSalvar;

    @Override
    public JpaRepository<Anuncio, Long> getRepository() {
        return anuncioRepository;
    }

    @Override
    public void preSave(Anuncio anuncio) {
        updateImagesAws(anuncio);
    }

    @Override
    public Anuncio salvar(Anuncio anuncio) throws Exception {
        if (isNull(anuncio.getId())) {
            anuncio.setDataPublicacao(LocalDate.now());
            anuncio.setStatus(Status.DISPONIVEL);
            anuncio.setUsuarioOrigem(usuarioService.getUsuarioLogado());
        }
        return save(anuncio);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void postSave(Anuncio anuncio) {
        deleteOldImages();
        saveNewImages(anuncio);
    }

    @Override
    public void excluir(Long id) {
        try {
            var anuncio = findAnuncioById(id);
            anuncio.setUsuarioDestino(null);
            anuncio.setUsuarioOrigem(null);
            anuncio.setUsuarioInstituicao(null);
            imagensParaExcluir = anuncio.getImagens();
            delete(anuncio);
            deleteOldImages();
        } catch (RecursoNaoPermitidoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Anuncio> findAnunciosByFilter(AnuncioFilter anuncioFilter) {
        anuncioFilter.setUsuarioLogado(usuarioService.getUsuarioLogado());
        var anuncios = anuncioRepository.findAnunciosByFilter(anuncioFilter);
        setUsuarioLogadoEh(anuncios, anuncioFilter.getUsuarioLogado());
        return anuncios;
    }

    private void setUsuarioLogadoEh(List<Anuncio> anuncios, Usuario usuarioLogado) {
        Optional.ofNullable(usuarioLogado)
                .ifPresent(usuario ->
                        anuncios.forEach(e -> setUsuarioLogadoEh(e, usuarioLogado)));
    }

    private void setUsuarioLogadoEh(Anuncio anuncio, Usuario usuarioLogado) {
        anuncio.setEhUsuarioOrigem(usuarioLogado.equals(anuncio.getUsuarioOrigem()));
        anuncio.setEhUsuarioDestino(usuarioLogado.equals(anuncio.getUsuarioDestino()));
        anuncio.setEhUsuarioInstituicao(usuarioLogado.equals(anuncio.getUsuarioInstituicao()));
    }

    @Override
    public void convidarInstituicao(String emailInstituicao) {
        this.envioEmailService.convidarInstituicaoEmail(emailInstituicao);
    }

    private void updateImagesAws(Anuncio anuncio) {
        imagensParaExcluir = null;
        imagensDataParaSalvar = null;
        if (anuncio.getId() != null) {
            imagensParaExcluir = getImagesDeleted(anuncio);
        }
        imagensDataParaSalvar = getImagensDataParaSalvar(anuncio.getImagens());
        anuncio.getImagens().removeAll(anuncio.getImagens().stream()
                .filter(imagemAnuncio -> imagemAnuncio.getUrl().startsWith("data")).toList());
    }

    private List<ImagemAnuncioSalvarDto> getImagensDataParaSalvar(List<ImagemAnuncio> imagens) {
        return imagens.stream().filter(imagemAnuncio -> imagemAnuncio.getUrl().startsWith("data"))
                .map(ImagemAnuncioSalvarDto::new)
                .collect(Collectors.toList());
    }

    private void deleteOldImages() {
        if (!isNullOrEmpty(imagensParaExcluir)) {
            imagensParaExcluir.forEach(imagem -> amazonS3BucketService.deleteFileFromBucket(imagem.getUrl().substring(60, 100), false));
            this.imagemAnuncioRepository.deleteAll(imagensParaExcluir);
        }
    }

    private void saveNewImages(Anuncio anuncio) {
        if (!isNullOrEmpty(imagensDataParaSalvar)) {
            imagensDataParaSalvar.forEach(imagem -> {
                try {
                    var file = new File(UUID.randomUUID() + ".png");
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagem.getImageData()));
                    ImageIO.write(bufferedImage, "png", file);
                    anuncio.getImagens().add(imagemAnuncioRepository.save(ImagemAnuncio.builder()
                            .url(amazonS3BucketService.uploadFile(file, false))
                            .nome(imagem.getNome())
                            .build()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            anuncioRepository.save(anuncio);
        }
    }

    private List<ImagemAnuncio> getImagesDeleted(Anuncio anuncio) {
        var imagensId = anuncio.getImagens().stream()
                .map(ImagemAnuncio::getId)
                .filter(Objects::nonNull).toList();
        var imagensDoBanco = anuncioRepository.findById(anuncio.getId())
                .orElseThrow(() -> new NoSuchElementException("Anúncio não encontrado."))
                .getImagens();
        return imagensDoBanco.stream().filter(img -> !imagensId.contains(img.getId())).toList();
    }

    @Override
    public Anuncio updateStatus(Anuncio anuncio) {
        var anuncioBanco = findById(anuncio.getId());
        var usuarioLogado = usuarioService.getUsuarioLogado();
        switch (anuncioBanco.getStatus()) {
            case DISPONIVEL:
                anuncioBanco.setStatus(Status.EM_NEGOCIACAO);
                if (!Operacao.DOACAO_PRODUTO.equals(anuncioBanco.getOperacao())) {
                    anuncioBanco.setUsuarioDestino(usuarioLogado);
                }
                break;
            case EM_NEGOCIACAO:
                if (Status.DISPONIVEL.equals(anuncio.getStatus())) {
                    anuncioBanco.setStatus(Status.DISPONIVEL);
                    anuncioBanco.setUsuarioDestino(null);
                } else if (Status.FINALIZADO.equals(anuncio.getStatus()) && usuarioLogado.equals(anuncioBanco.getUsuarioOrigem())) {
                    anuncioBanco.setStatus(Status.FINALIZADO);
                }
        }
        anuncioBanco = anuncioRepository.save(anuncioBanco);
        setUsuarioLogadoEh(anuncioBanco, usuarioLogado);
        return anuncioBanco;
    }

    public Anuncio findAnuncioById(Long id) throws RecursoNaoPermitidoException {
        var anuncio = findById(id);
        if (anuncio.getUsuarioOrigem().equals(usuarioService.getUsuarioLogado())) {
            return anuncio;
        }
        throw new RecursoNaoPermitidoException("Usuário autenticado não é o proprietário do anúncio.");
    }
}
